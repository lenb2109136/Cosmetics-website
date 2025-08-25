package com.example.e_commerce.model.chat;

import com.example.e_commerce.constants.MessageType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Message.class, name = "MESSAGE"),
    @JsonSubTypes.Type(value = DropHeart.class, name = "DROPHEART"),
    @JsonSubTypes.Type(value = ViewMessage.class, name = "VIEWMESSAGE"),
    @JsonSubTypes.Type(value = UserAcTivity.class, name = "USERACTIVITY"),
    @JsonSubTypes.Type(value = MoutUserChat.class, name = "MOUNTUSER")
})
public abstract  class chatRoot {
	private MessageType type;
    public void setType(MessageType type) {
		this.type = type;
	}
	public MessageType getType() {
        return type;
    }
}
