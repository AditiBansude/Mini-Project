#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <Adafruit_Fingerprint.h>
#include <HardwareSerial.h>
#include <LiquidCrystal_I2C.h>
#include <ESP32Servo.h>
#include "HX711.h"

#include <AsyncTCP.h>
#include <ESPAsyncWebServer.h>

#define RX_PIN 16
#define TX_PIN 17
#define SERVO_PIN 19
#define HX711_DT 18
#define HX711_SCK 23

const char* ssid = "WiFi_SSID"; //Replace with your Wi-Fi SSID
const char* password = "Password"; //Replace with your Wi-Fi Password
const char* matchURL = "http://192.168.42.208:8080/fingerprint/match";

// Static IP config
IPAddress local_IP(192, 168, 1, 100);
IPAddress gateway(192, 168, 1, 1);
IPAddress subnet(255, 255, 255, 0);
IPAddress dns(8, 8, 8, 8);

HardwareSerial mySerial(2);
Adafruit_Fingerprint finger(&mySerial);
LiquidCrystal_I2C lcd(0x27, 16, 2);
Servo servo;
HX711 scale;

// Web server
AsyncWebServer server(80);
AsyncEventSource events("/events");

void sendLog(String msg) {
  Serial.println(msg);
  events.send(msg.c_str(), "message", millis());
}

// HTML page for web view
const char* htmlPage = R"rawliteral(
<!DOCTYPE html>
<html>
<head>
  <title>ESP32 Serial Monitor</title>
  <meta charset="UTF-8">
  <style>
    body { font-family: monospace; background: #111; color: #0f0; padding: 20px; }
    #log { white-space: pre-wrap; border: 1px solid #0f0; padding: 10px; height: 400px; overflow-y: scroll; }
  </style>
</head>
<body>
  <h2>ESP32 Serial Monitor</h2>
  <div id="log">Waiting for data...</div>
  <script>
    const log = document.getElementById('log');
    const source = new EventSource('/events');
    source.onmessage = function(event) {
      log.innerText += event.data + "\\n";
      log.scrollTop = log.scrollHeight;
    };
  </script>
</body>
</html>
)rawliteral";

void setup() {
  Serial.begin(115200);
  mySerial.begin(57600, SERIAL_8N1, RX_PIN, TX_PIN);
  lcd.init(); lcd.backlight();
  servo.attach(SERVO_PIN);
  servo.write(0);

  scale.begin(HX711_DT, HX711_SCK);
  scale.set_scale(593.f);
  scale.tare();

  // WiFi setup
  WiFi.config(local_IP, gateway, subnet, dns);
  WiFi.begin(ssid, password);
  lcd.setCursor(0, 1); lcd.print("Connecting WiFi...");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500); Serial.print(".");
  }

  lcd.clear(); lcd.setCursor(0, 0); lcd.print("WiFi Connected");
  sendLog("ESP32 IP: " + WiFi.localIP().toString());

  // Fingerprint setup
  finger.begin(57600);
  if (finger.verifyPassword()) {
    sendLog("Fingerprint sensor found.");
    lcd.setCursor(0, 1); lcd.print("Sensor ready");
  } else {
    sendLog("Sensor not detected.");
    lcd.setCursor(0, 1); lcd.print("Sensor error!");
    while (true) delay(1);
  }

  // Webserver routes
  server.on("/", HTTP_GET, [](AsyncWebServerRequest *request){
    request->send(200, "text/html", htmlPage);
  });

  events.onConnect([](AsyncEventSourceClient *client){
    if (client->lastId()) {
      Serial.printf("Client reconnected! Last message ID that it got is: %u\n", client->lastId());
    }
    client->send("Connected to ESP32", NULL, millis(), 1000);
  });

  server.addHandler(&events);
  server.begin();

  delay(2000);
  lcd.clear(); lcd.print("Press 'm' to"); lcd.setCursor(0, 1); lcd.print("match finger");
}

void loop() {
  if (Serial.available()) {
    char option = Serial.read();
    if (option == 'm') {
      matchFingerprint();
    }
  }
}

void matchFingerprint() {
  lcd.clear(); lcd.print("Place Finger...");
  sendLog("Place finger for matching...");
  while (finger.getImage() != FINGERPRINT_OK);

  if (finger.image2Tz(1) != FINGERPRINT_OK) {
    sendLog("Couldn't convert image.");
    lcd.clear(); lcd.print("Conversion failed");
    return;
  }

  if (finger.fingerFastSearch() == FINGERPRINT_OK) {
    int matchedID = finger.fingerID;
    sendLog("Match found! ID: " + String(matchedID));
    lcd.clear(); lcd.print("Authenticated");
    sendMatchToBackend(matchedID);
  } else {
    sendLog("No match found.");
    lcd.clear(); lcd.print("No match found");
  }
}

void sendMatchToBackend(int matchedID) {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(matchURL);
    http.addHeader("Content-Type", "application/json");

    String body = "{\"memberId\":" + String(matchedID) + "}";
    int code = http.POST(body);

    if (code == 200) {
      String payload = http.getString();
      sendLog("Matched user details received:");
      float targetWeight = parseAndPrintResponse(payload);
      if (targetWeight > 0) {
        startDispensing(targetWeight);
      }
    } else {
      sendLog("Server error: " + String(code));
      lcd.clear(); lcd.print("Server error");
    }
    http.end();
  } else {
    sendLog("WiFi not connected.");
    lcd.clear(); lcd.print("WiFi error!");
  }
}

float parseAndPrintResponse(String jsonResponse) {
  StaticJsonDocument<8192> doc;
  DeserializationError error = deserializeJson(doc, jsonResponse);
  if (error) {
    sendLog("JSON parsing failed: " + String(error.f_str()));
    lcd.clear(); lcd.print("JSON error!");
    return 0;
  }

  JsonObject matchedMember = doc["matchedMember"];
  sendLog("\nScanned Member Details:");
  sendLog("Member ID: " + String(matchedMember["memberId"].as<int>()));
  sendLog("Full Name: " + matchedMember["fullName"].as<String>());
  sendLog("Aadhar Number: " + matchedMember["aadharNumber"].as<String>());
  sendLog("Contact Number: " + matchedMember["contactNumber"].as<String>());
  sendLog("Household ID: " + matchedMember["householdId"].as<String>());

  JsonObject household = doc["household"];
  sendLog("\nHousehold Details:");
  sendLog("Family Head: " + household["familyHead"].as<String>());
  sendLog("Allocated Ration: " + String(household["allocatedRation"].as<float>()));
  sendLog("Ration Status: " + String(household["rationStatus"].as<bool>()));

  JsonArray familyMembers = doc["familyMembers"].as<JsonArray>();
  sendLog("\nFamily Members:");
  for (JsonObject member : familyMembers) {
    sendLog("---------------------------");
    sendLog("Member ID: " + String(member["memberId"].as<int>()));
    sendLog("Full Name: " + member["fullName"].as<String>());
    sendLog("Aadhar Number: " + member["aadharNumber"].as<String>());
    sendLog("Contact Number: " + member["contactNumber"].as<String>());
  }

  return household["allocatedRation"].as<float>() / 1000.0;
}

void startDispensing(float targetKg) {
  lcd.clear(); lcd.print("Dispensing...");
  sendLog("Dispensing started.");
  servo.write(90);
  delay(1000);

  float weight = 0;
  while (weight < targetKg) {
    weight = scale.get_units();
    if (weight < 0.02) weight = 0;

    lcd.setCursor(0, 1);
    lcd.print("Weight: ");
    lcd.print(weight, 2);
    lcd.print("kg ");
    sendLog("Live Weight: " + String(weight, 2));
    delay(500);
  }

  servo.write(0);
  lcd.clear(); lcd.print("Dispensed");
  delay(2000);
  lcd.clear(); lcd.print("Complete");
  sendLog("Dispensing done.");
}