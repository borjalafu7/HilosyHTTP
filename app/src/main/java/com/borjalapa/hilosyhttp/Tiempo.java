package com.borjalapa.hilosyhttp;

class Tiempo {
    public final Double temperatura;
    public final String descripcion;
    public final Integer humedad;

    public Tiempo(Double temperatura, String descripcion, Integer humedad) {
        this.temperatura = temperatura;
        this.descripcion = descripcion;
        this.humedad = humedad;
    }

    public Double kelvinToCelsius(Double tempKelvin){
        return Math.round((tempKelvin - 273.15)*100.0)/100.0;
    }
}