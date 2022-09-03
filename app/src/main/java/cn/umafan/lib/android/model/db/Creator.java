package cn.umafan.lib.android.model.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity(createInDb = false)
public class Creator {
    @Id
    @Property(nameInDb = "id")
    private Long id;

    @Property(nameInDb = "names")
    private String names;

    @Generated(hash = 1493267542)
    public Creator(Long id, String names) {
        this.id = id;
        this.names = names;
    }

    @Generated(hash = 908439796)
    public Creator() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNames() {
        return this.names;
    }

    public void setNames(String names) {
        this.names = names;
    }
}
