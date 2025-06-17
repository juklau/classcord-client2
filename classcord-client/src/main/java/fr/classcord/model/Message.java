package fr.classcord.model;

import org.json.JSONObject;

public class Message{
    public String type;
    public String subtype;
    public String from;
    public String to;
    public String content;
    public String timestamp;



     // Constructeurs

    //constructeur vide => il faut utiliser les setter
    // public Message(){

    // }

    public Message(String type, String subtype, String from, String to, String content, String timestamp){
        this.type = type;
        this.subtype = subtype;
        this.from = from;
        this.to = to;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Message(String type, String subtype, String from, String content){
        this.type = type;
        this.subtype = subtype;
        this.from = from;
        this.content = content;
    }



    // getters/setters
    public String getType() {
        //this => fait référence à l'attribut de la classe// fait référence à l'objet courant
        // on parle de l'attribut de l'objet=> plus clair
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return this.subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return this.to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

   

    //methodes
   
    //toujours faire toString
    //igy fogja kiirni, amikor hivom System.out.println();

    
    // @Override
    // public String toString (){ //écriture en json
    //     return "Message{" +
    //         "type='" + type + '\'' +
    //         ", subtype = '" + subtype + '\'' +
    //         ", from = '" + from + '\'' +
    //         ", to = '" + to + '\''+
    //         ", content = '" + content + '\'' +
    //         ", timestamp = '" + timestamp + '\'' +
    //     '}';
    // }

     @Override
    public String toString (){ //écriture en json
        return "Message{" +
            "content ='" + content + '\'' +
        '}';
    }

    //convertir l'objet message en JSONObject
    public JSONObject toJson(){
        JSONObject json = new JSONObject();
        json.put("type" , type);
        json.put("subtype" , subtype);
        json.put("from", from);
        json.put("to", to);
        json.put("content", content);
        json.put("timestamp", timestamp);

        return json;
    }

    //Convertir une chaîne JSON en objet Message
    public Message fromJson(String jsonString){
        JSONObject json = new JSONObject(jsonString);
        return new Message(
            
            json.getString("type"),
            // json.getString("subtype"),
            "global",
            json.getString("from"),
            // json.getString("to"),
            "global",
            json.getString("content"),
            json.getString("timestamp")
        );
    }


}

    







