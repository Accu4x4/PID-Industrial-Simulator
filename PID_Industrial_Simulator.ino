#include <SPI.h>

#define SELADC 10    // chip-MCP3302
#define SELDAC 8     // chip-MCP4822
#define DATAOUT 11   // MOSI 
#define DATAIN 12    // MISO 
#define SPICLOCK 13  // Clock
#define DACSET 4096  // VOUT-A, max out 4.096V, output enable 
#define AnalogIN A3

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
char C;

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
  Serial.begin(9600);
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
    else if(C=='M') {
      incomingData = Serial.parseInt();  // If serial data begins with M = DAC value
    }
    else if(C=='S') {                // If serial data begins with S = setpoint
      incomingByte = Serial.parseFloat();
    }
    else if(C=='P') {           // If serial data begins with P = Kp
      Kp = Serial.parseFloat();
    }
    else if(C=='I') {           // If serial data begins with I = Ki
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
      A = read_adc();
      VoltMCP = VoltMCP+A;
      delay(1);
    }
    VoltMCP = VoltMCP/10;
    //A = read_adc();
    //B = analogRead(A0);
    //VoltMCP = (float(A)*5.03)/4095;
    //VoltARD = (float(B)*5.06)/1023; 
    //Serial.println(VoltARD*100, 3);
    Serial.println(VoltMCP);
    delay(1);
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
    Serial.println(analogInValue);
    V=(analogInValue*5)/1023;
    e=incomingByte-V;
    integral=integral+e*dT;
    if(integral > 5) {
      integral=5;
    }
    else if(integral < 0) {
      integral=0;
    }
    out=Kp*e+Ki*integral;
    bias=out;
    digitalOutput=(out*255)/5;
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
  }
}

/* Write value to MCP4822 DAC function*/
int writeDAC(int value) {
  SPI.beginTransaction(SPISettings(4000000, MSBFIRST, SPI_MODE0));
  sendData=12288+value;
  digitalWrite(SELDAC, LOW);
  SPI.transfer16(sendData);
  digitalWrite(SELDAC, HIGH);
  SPI.endTransaction();
  return 0;
}
/* Read value from MCP3302 ADC */
// maximum clock frequency is 2.1 MHz @ 5V
int read_adc(){
  SPI.beginTransaction(SPISettings(1000000, MSBFIRST, SPI_MODE0));
  byte commandbits1 = B00001000;
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
