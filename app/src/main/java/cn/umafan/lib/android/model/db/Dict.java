package cn.umafan.lib.android.model.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

@Entity(createInDb = false)
public class Dict implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Property(nameInDb = "id")
    private Long id;

    @Property(nameInDb = "class")
    private String classType;

    @Property(nameInDb = "desc")
    private String desc;

    @Property(nameInDb = "key")
    private String key;

    @Property(nameInDb = "refId")
    private Integer refId;

    @Property(nameInDb = "related")
    private String related;

    @Property(nameInDb = "relatedId")
    private Integer relatedId;

    @Generated(hash = 1019137228)
    public Dict(Long id, String classType, String desc, String key, Integer refId,
            String related, Integer relatedId) {
        this.id = id;
        this.classType = classType;
        this.desc = desc;
        this.key = key;
        this.refId = refId;
        this.related = related;
        this.relatedId = relatedId;
    }

    @Generated(hash = 1138334630)
    public Dict() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassType() {
        return this.classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getRefId() {
        return this.refId;
    }

    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    public String getRelated() {
        return this.related;
    }

    public void setRelated(String related) {
        this.related = related;
    }

    public Integer getRelatedId() {
        return this.relatedId;
    }

    public void setRelatedId(Integer relatedId) {
        this.relatedId = relatedId;
    }
}
