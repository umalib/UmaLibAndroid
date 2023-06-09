package cn.umafan.lib.android.model.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

@Entity(createInDb = false)
public class Rec implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Property(nameInDb = "id")
    private Long id;

    @Property(nameInDb = "refId")
    private Long refId;

    @Property(nameInDb = "title")
    private String title;

    @Property(nameInDb = "others")
    private String others;

    @Property(nameInDb = "type")
    private Integer type;

    @Property(nameInDb = "r")
    private Integer r;

    @Property(nameInDb = "name")
    private String name;

    @Property(nameInDb = "reason")
    private String reason;

    @Generated(hash = 1774498343)
    public Rec(Long id, Long refId, String title, String others, Integer type,
            Integer r, String name, String reason) {
        this.id = id;
        this.refId = refId;
        this.title = title;
        this.others = others;
        this.type = type;
        this.r = r;
        this.name = name;
        this.reason = reason;
    }

    @Generated(hash = 424129248)
    public Rec() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRefId() {
        return this.refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOthers() {
        return this.others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getR() {
        return this.r;
    }

    public void setR(Integer r) {
        this.r = r;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
