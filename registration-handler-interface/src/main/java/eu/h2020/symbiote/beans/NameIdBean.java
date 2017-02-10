package eu.h2020.symbiote.beans;

import com.google.gson.annotations.Expose;

import org.springframework.data.annotation.Id;

/**
 * Created by jose on 27/09/16.
 */
public class NameIdBean {

    @Id
    @Expose(serialize = false, deserialize = false)
    private String internalId;

    @Expose(serialize = false, deserialize = false)
    private String id;

    private String name;

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
