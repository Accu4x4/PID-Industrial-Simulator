#include <SPI.h>

// definition of arduino pins
#define SELADC 10    // chip-MCP3302
#define SELDAC 8     // chip-MCP4822
#define DATAOUT 11   // MOSI 
#define DATAIN 12    // MISO 
#define SPICLOCK 13  // Clock
#define DACSET 4096  // VOUT-A, max out 4.096V, output enable 
#define AnalogIN A3

// definition of initial letters received from serial
#define DAC_value 'M'
#define setpoint_value 'S'
#define proportional_value 'P'
#define integral_value 'I'
#define mode_1 'W'
#define mode_2 'X'
#define mode_3 'Y'
#define mode_4 'Z'
#define mode_exit 'Q'
#define connection_confirm 'H'

float incomingByte;   // for incoming serial data
float analogInValue;

/* PID variables */
float V, e, out;
float Kp=1, Ki=0, integral=0, bias=0, dT;
int nowTime, deltaT;
/*************************/

long ticks = 0;
int readvalue, A, B, sendData, incomingData=0, hi=0, low=0;
int reading;
int b1=0, b2=0, previous=0, i; 
unsigned long VoltMCP=0;
float VoltARD=0;
int digitalOutput=0;
int mode=0;     // Used to define the mode
char C, DAC_channel;

void setup() {
  //set pin modes 
  pinMode(SELADC, OUTPUT); 
  pinMode(SELDAC, OUTPUT); 

  // disable device to start with 
  digitalWrite(SELADC, HIGH); 
  digitalWrite(SELDAC, HIGH);
  analogWrite(5,0);
  nowTime = micros();
  
  //Serial and SPI initialization
  SPI.begin();
  Serial.begin(115200);
}

void loop() {
  if (Serial.available()>0) {
    C = Serial.read();
    if(C=='W') {      // Char W, X, Y, Z received from processing
      mode=1;         // define mode 1, 2, 3, 4
    }
    else if(C=='X') {
      mode=2;
    }
    else if(C=='Y') {
      mode=3;
    }
    else if(C=='H'){
      delay(10);
      Serial.println("Hello!");
    }
    else if(C == 'Q'){
      mode = 0;
    }
    else if(C==DAC_value) {
      C = Serial.read();                        // Read what channel to use (A/B)
      incomingData = Serial.parseInt();         // If serial data begins with M = DAC value
      DAC_channel = C;                          // Store the channel selected by user
      mode = 1;        
    }
    else if(C==setpoint_value) {                // If serial data begins with S = setpoint
      incomingByte = Serial.parseFloat();
    }
    else if(C==proportional_value) {            // If serial data begins with P = Kp
      Kp = Serial.parseFloat();
    }
    else if(C==integral_value) {                // If serial data begins with I = Ki
      Ki = Serial.parseFloat();
    }
    //Serial.print("I received: ");
    //Serial.println(incomingByte);
    //analogWrite(5,incomingByte);
  }
  
  // Check which mode has be chosen and enter the appropriate loop
  if (mode == 1) {
    
    if(incomingData != previous) {
      writeDAC(incomingData); 
      previous=incomingData; 
    }
    for(i=0; i<10; i++) {
      A = read_adc('A');
      VoltMCP = VoltMCP+A;
      delay(1);
    }
    VoltMCP = VoltMCP/10;  
    Serial.print((String)VoltMCP+"A");
    VoltMCP = 0;

    for(i=0; i<10; i++) {
      A = read_adc('B');
      VoltMCP = VoltMCP+A;
      delay(1);
    }
    VoltMCP = VoltMCP/10;  
    Serial.print((String)VoltMCP+"B");
    VoltMCP = 0;
    // do whatever you want with these readings

    //long tcnv = millis() - ticks;

    //delay(100 - tcnv);
  }
  else if (mode == 2) {
    deltaT=micros()-nowTime;
    dT=(float)deltaT/1000000;
    nowTime=micros();
    analogInValue=analogRead(AnalogIN);
    Serial.print((String)analogInValue+"D");
    V=(analogInValue*5.08)/1023;
    e=incomingByte-V;
    integral=integral+e*dT;
    if(integral > 5.08) {
      integral=5.08;
    }
    else if(integral < 0) {
      integral=0;
    }
    out=Kp*e+Ki*integral;
    bias=out;
    digitalOutput=(out*255)/5.08;
    if(digitalOutput >= 255)
    {
      analogWrite(5,255); 
    }
    else if(digitalOutput <= 0) 
    {
      analogWrite(5,0);
    }
    else {
      analogWrite(5, digitalOutput);
    }
    delay(20);  
  }
}

/* Write value to MCP4822 DAC function*/
int writeDAC(int value) {
  SPI.beginTransaction(SPISettings(4000000, MSBFIRST, SPI_MODE0));
  if(DAC_channel == 'A'){
    sendData=12288+value;    
  }
  else if(DAC_channel == 'B'){
    sendData=45056+value; 
  }
  digitalWrite(SELDAC, LOW);
  SPI.transfer16(sendData);
  digitalWrite(SELDAC, HIGH);
  SPI.endTransaction();
  return 0;
}
/* Read value from MCP3302 ADC */
// maximum clock frequency is 2.1 MHz @ 5V
int read_adc(char ch){
  SPI.beginTransaction(SPISettings(1000000, MSBFIRST, SPI_MODE0));
  // B00001000 for channel A, B00001001 for channel B
  byte commandbits1;
  if(ch == 'A'){
    commandbits1 = B00001000;
  }
  else{
    commandbits1 = B00001001;
  }
  byte commandbits2 = B00000000;
  digitalWrite (SELADC, LOW); // Select adc

  // first byte
  // first byte will always be B000010xx where xx are the D2 and D1 channel bits  

  SPI.transfer(commandbits1);       // send out first byte of command bits

  // second byte; Bx0000000; leftmost bit is D0 channel bit
  b1 = SPI.transfer(commandbits2);       // send out second byte of command bits
  b2 = SPI.transfer(b2);              // don't care what we send
  digitalWrite(SELADC, HIGH); // turn off device

  // hi byte will have XX0SBBBB
  hi = b1 & B00001111;

  // read low byte
  low = b2;

  reading = hi * 256 + low;
  SPI.endTransaction();

  return (reading);
}
