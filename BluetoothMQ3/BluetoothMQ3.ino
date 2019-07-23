  #include <SoftwareSerial.h>
  
  const int RX = 11;
  const int TX = 10;
  SoftwareSerial BT(TX,RX);
  
  // Pines
  const int buttonTurnOnCar = 4;
  const int ledDontTunrOnCar = 9;
  const int ledTunrOnCar = 13;
  const int sensorGasDigital = 8;
  const int sensorGasAnalogico = A1;
  
  int estadoSensorDigital = 0;     
  int buttonState = 0;
  
  //variable para leer el estado del pin  
  void setup()
  {
    setPins();
    BT.begin(9600); 
    Serial.begin(9600);  
    BT.println("MODULO CONECTADO");  
    BT.print("#"); 
  }
  void loop()
  {   
    
     int lecturaAnalogicaGas = analogRead(sensorGasAnalogico); //Lemos la salida analógica  del MQ
     Serial.print("\nLectura Analogica MQ3 en mg/L: ");
     Serial.print(lecturaAnalogicaGas);
     
     estadoSensorDigital = digitalRead(sensorGasDigital);                         //  lea el valor de la entrada 
     Serial.print("\tEstado: ");
     Serial.println(estadoSensorDigital);
     
     float voltaje = getVoltage(lecturaAnalogicaGas);
     
     buttonState = digitalRead(buttonTurnOnCar);
     if (buttonState == HIGH){
        if(estadoSensorDigital != HIGH ){ 
            detectoAlcohol(voltaje);
            Serial.print("PRESENCIA DE ALCOHOL");
            delay(30000);
        } else {
            digitalWrite(ledTunrOnCar, HIGH);
            sendMessageBT("No se detecta alcohol");
            Serial.print("SIN PRESENCIA DE ALCOHOL");
            delay(1000);
        }
     } else {
       digitalWrite(ledTunrOnCar, LOW);
       digitalWrite(ledDontTunrOnCar, LOW);
       sendMessageBT("Motor apagado");
       Serial.print("Motor apagado"); 
     }
     
     delay(1000); 
  }
  
  void detectoAlcohol(int lecturaAnalogicaGas) {
    digitalWrite(ledTunrOnCar, HIGH);
    digitalWrite(ledDontTunrOnCar, HIGH);
    delay(100);
    // String buf = String(lecturaAnalogicaGas, 1);
    sendMessageBT(String(lecturaAnalogicaGas)); 
    Serial.print("ALCOHOL PRESENTE"); 
  }
  
  void setPins(){
    pinMode(buttonTurnOnCar, INPUT); 
    pinMode(sensorGasDigital, INPUT);                   //se declara el pin 8 como entrada  
    pinMode(ledTunrOnCar, OUTPUT);
    pinMode(ledDontTunrOnCar, OUTPUT);
  }
  
  void sendMessageBT(String message){
    BT.print(message + '#');
  }
  
  float getVoltage(int lecturaAnalogicaGas){
    float voltaje = lecturaAnalogicaGas * (5.0 / 1023.0); //Convertimos la lectura en un valor de voltaje
    Serial.print("\t\tVoltaje: ");
    Serial.println(voltaje);
    return voltaje;
  }
