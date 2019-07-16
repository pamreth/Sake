
//Código para enviar aviso al celular sobre la presencia de  alcohol por medio de bluetooth

#include <SoftwareSerial.h> 
SoftwareSerial BT(10,11);
int estado = 0;     


//variable para leer el estado del pin  
void setup()
{
   pinMode(8, INPUT);                   //se declara el pin 8 como entrada  
   pinMode(13, OUTPUT);
   BT.begin(9600); 
   Serial.begin(9600);  
   BT.println("MODULO CONECTADO");  
   BT.print("#"); 
}
void loop()
{   
  
    int adc_MQ = analogRead(A1); //Lemos la salida analógica  del MQ
    float voltaje = adc_MQ * (5.0 / 1023.0); //Convertimos la lectura en un valor de voltaje
    Serial.print("Lectura Analogica MQ3 en mg/L: ");
    Serial.print(adc_MQ);
    estado = digitalRead(8);                         //  lea el valor de la entrada 
    Serial.print("\tEstado: ");
    Serial.println(estado);
    if(estado != HIGH ){ 
        digitalWrite(13, HIGH);+
        delay(100);
        String buf = String(adc_MQ, 1);
        BT.println(adc_MQ); 
        Serial.print("LED ENCENDIDO"); 
        BT.print("#");
        delay(30000);
    } 
    else {
        digitalWrite(13, LOW);
        delay(100); 
        BT.print("No se detecta alcohol#"); 
        Serial.print("LED APAGADO#");
    }
    Serial.print("\t\tVoltaje: ");
    Serial.println(voltaje);
    delay(1000); 
  
}

