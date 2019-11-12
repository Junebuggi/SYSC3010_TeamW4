#define serialPi Serial

//Declare constant variables
const int potID = 1; //Each pot has its own unique identifier hardcoded
const double ldrLower = 390; //the value when the LDR is complete darkness
const double ldrUpper = 685; //the value when the LDR is in complete brightness
const double ldrRange = ldrUpper-ldrLower; //Used to map a percentage of light detected
const float criticalDistance = 20; // The pump will not turn on to prevent hardware damages 
                                   // if the waterDistance detected is below this value  
//Declare Sensor and Pump Pins
const int ldrPin = A0;
const int trigPin = 10;
const int echoPin = 9;
const int soilMoisturePin = A2;
const int pumpPin = 7;

//Declare debugging LED pins
const int ldrLED = 13;
const int distanceLED = 11;
const int soilMoistureLED = 8; 
const int pumpLED = 6;
const int ackLED = 5;

// Define global variables for waterPump
float initialDistance; // The initial waterDistance when the pump begins to run
int waterPumpDuration; // How long in seconds the pump is set to run for
boolean waterPumpStatus; // Set to false in timer ISR no water was dispensed, otherwise true

//Function prototypes
String getSensorData(void);
float readUltraSonic(void);
void getLDR(void);
void getWaterLevel(void);
void getSoilMoisture(void);
void waterPumpManager(void);

void setup() {
  
  //Set the modes for the debuggin LEDs as outputs
  pinMode(ldrLED, OUTPUT);
  pinMode(distanceLED, OUTPUT);
  pinMode(soilMoistureLED, OUTPUT);
  pinMode(pumpLED, OUTPUT);
  pinMode(ackLED, OUTPUT);
  
  //Set the modes for the sensor and pump pins
  pinMode(ldrPin, INPUT);
  pinMode(soilMoisturePin, INPUT);
  pinMode(trigPin, OUTPUT); // Sets the trigPin as an Output
  pinMode(echoPin, INPUT); // Sets the echoPin as an Input
  pinMode(pumpPin, OUTPUT);

  // Initialize timer1 for the water pump
  cli(); // disable global interrupts
  TCCR1A = 0; // set entire register to 0
  TCCR1B = 0; // set entire register to 0
  OCR1A = 15624; // set compare match register for 1 second
  TCCR1B |= (1 << WGM12); // turn on CTC mode 
  TCCR1B |= (1 << CS10) | (1 << CS12); // Set CS10 and CS12 bits for 1024 prescaler
  sei(); // enable global interrupts
  TIMSK1 &= ~(1 << OCIE1A); // disable timer compare interrupt

  serialPi.begin(9600); //begin serial on port 9600

}

/*
 * The arduino checks if the serial is available. When a message is sent from the roomPi, the arduino reads
 * the opcode and executes the appropriate commands. If the "E" opcode is received then the arduino sends the
 * potSensorData in a JSON format. If the "C" opcode is received the arduino turns on the water pump for the 
 * specified time.
 */
void loop() {
  
  boolean flag = false;
  if(serialPi.available() > 0){ 
    String opcode = serialPi.readStringUntil(',');
    if (opcode == "E") { // The roomPi is requesting potSensorData
      String packet = getSensorData(); 
      serialPi.println(packet);
      waterPumpStatus = true; //reset the waterPumpStatus if it was set to false
      int startWaitingTime = millis(); 
      ackRec = false; // ACK received flag 
      while (millis() < startWaitingTime + 1000 && !ackRec){ //Try for 1 second to resend the packet if no ACK from roomPi
        delay(100); //Allow time for the roomPi to send the ACK
        if (serialPi.available() > 0 ) { 
          opcode = serialPi.readStringUntil(',');
          if (opcode == "0") {
            digitalWrite(ackLED, !digitalRead(ackLED));
            ackRec = true; // The acknowledgment was received from the roomPi, set flag
          }
        }
        else {
          serialPi.println(packet); //If no ACK was received resend the packet
        }
      }
    }

    else if (opcode = "C") { // The roomPi is requesting for the water pump to be turned on
      
        waterPumpDuration = serialPi.readStringUntil('\n').toInt();
        float distance = readUltraSonic();
        
        if(waterPumpDuration >= 1 && (criticalDistance - distance) > 0.1) {
          static int timerCount = 0; //Initialize timerCount to 0 first time through
          digitalWrite(pumpLED, HIGH); //For testing purposes, remove later
          digitalWrite(pumpPin, LOW);
          initialDistance = readUltraSonic(); // Find the initial water level
          TIMSK1 |= (1 << OCIE1A); // Enable the timer for the water pump
        }  
    }
  }

 //while(serialPi.available() > 0) { serialPi.read(); } //Flush any leftover values
  
}
/**
 * Polls all the sensors and adds the data into a string in the JSON format to be
 * sent to the roomPi
 * 
 * @return a String of the potSensorsData in a JSON format
 */
String getSensorData(void) {
  
  packet = "{\"opcode\": 8, \"potID\": " + String(potID);
  packet += ",";
  packet += "\"waterDistanceStatus\": " + String(waterDistanceStatus) + ",";
  getWaterLevel();
  getLDR();
  getSoilMoisture();
  packet += "}";
  return packet;
  
}

/**
 * Polls the ultraSonic sensor to get the waterDistance and adds this to the 
 * String to be sent to the roomPi
 */
void getWaterLevel(void){

  float distance = readUltraSonic();
  boolean waterDistanceStatus;
  
  packet += "\"waterDistance\": " + String(distance);
  packet += ",";

  //If no voltage is supplied to the ultrasonic sensor then turn on the debugging LED 
  if(abs(distance - 0) < 0.01){
    digitalWrite(distanceLED, HIGH);
    waterDistanceStatus = false;
  }
  else{
    digitalWrite(distanceLED, LOW); 
    waterDistanceStatus = true;  
  }
}

/**
 * Polls the ldr to get the light value and adds this to the 
 * String to be sent to the roomPi
 */
void getLDR(void){
   int ldrValue = analogRead(ldrPin);
   boolean ldrStatus;

   packet += "\"light\": " + String(((ldrValue-ldrLower)/ldrRange * 100)) + ",";
   
   //If no voltage is supplied to the ldr then turn on the debugging LED  
   if ((ldrValue != 0) {
    digitalWrite(ldrLED, LOW);
    ldrStatus = true;
   } else {
    digitalWrite(ldrLED, HIGH;
    ldrStatus = false;
   }
   
  packet += "\"ldrStatus\": " + String(ldrStatus) + ",";
   
}
/**
 * Polls the ultraSonic sensor
 * 
 * @return the waterDistance in cm, represented as a float
 */
float readUltraSonic(void){
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  // Sets the trigPin on HIGH state for 10 micro seconds
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  // Reads the echoPin, returns the sound wave travel time in microseconds
  long duration = pulseIn(echoPin, HIGH);
  // Calculating the distance
  return (duration*0.034/2); //The distance to the water 
}

/**
 * Polls the soil moisture sensor to get the soilMoisture and adds this to the 
 * String to be sent to the roomPi
 */
void getSoilMoisture(void){
  int sensorValue = analogRead(soilMoisturePin); 
  boolean soilMoistureStatus;
  packet += "\"soilMoisture\": " + String(sensorValue) + ",";

  //If no voltage is supplied to the moisture sensor then turn on the debugging LED 
  if (analogRead(soilMoisturePin) == 0){ 
    digitalWrite(soilMoistureLED, HIGH);
    soilMoistureStatus = false;
  } else{
    digitalWrite(soilMoistureLED, LOW);
    soilMoistureStatus = true;
  }
  packet += "\"soilMoistureStatus\": " + String(soilMoistureStatus);
}

/**
 * The timer ISR used to turn off the water pump. When the water pump is turned on the 
 * ISR runs every second and turns off the pump if it has ran for pumpDuration seconds or
 * if there is not enough water in the tank.
 */
ISR(TIMER1_COMPA_vect){
  timerCount++;
  float distance = readUltraSonic();
  if ((criticalDistance - distance) <= 0.1) { //Turn the pump off if there isn't enough water
    digitalWrite(pumpPin, HIGH);
    TIMSK1 &= ~(1 << OCIE1A); //Disable the timer
    timerCount = 0; //Reset timerCount for the next time
    return
  }
  if (seconds >= waterPumpDuration || ) {
    digitalWrite(pumpPin, HIGH);
    TIMSK1 &= ~(1 << OCIE1A); //Disable the timer
    timerCount = 0; //Reses timerCount for the next time
    if ( (readUltraSonic() - initialDistance) >= 0.1) {
       // The water levels did not decrease when the pump was suppose to be on
       waterPumpStatus = false
    }
  } 
}
