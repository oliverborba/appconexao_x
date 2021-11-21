package com.borba.appconexao_x;

public class Event {

    public final String familia;
    public final String ultimaMod;
    public final String categoria;

    public Event(String eventFamilia, String eventUltimaMod, String eventCategoria) {
        familia = eventFamilia;
        ultimaMod = eventUltimaMod;
        categoria = eventCategoria;
    }
}
