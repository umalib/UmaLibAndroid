package cn.umafan.lib.android.model.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(createInDb = false, nameInDb = "article")
public class ArtCreator {
    @Id
    @Property(nameInDb = "id")
    private Long id;

    @Property(nameInDb = "author")
    private String author;
    @Property(nameInDb = "translator")
    private String translator;

    @Generated(hash = 1069894656)
    public ArtCreator(Long id, String author, String translator) {
        this.id = id;
        this.author = author;
        this.translator = translator;
    }

    @Generated(hash = 2043771935)
    public ArtCreator() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTranslator() {
        return this.translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }
}
