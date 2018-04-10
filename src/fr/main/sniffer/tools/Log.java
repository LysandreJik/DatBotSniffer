package fr.main.sniffer.tools;

public class Log {

    public Log()  { }

    public static void writeLogDebugMessage (String msg)
    {
        System.out.println("--- "+ msg +" ---");
    }

    public static void writeLogCTSMessage (String msg)
    {
        System.out.println("Client->Serveur : "+ msg);
    }
    public static void writeLogSTCMessage (String msg)
    {
        System.out.println("Serveur->Client : "+ msg);
    }

}
