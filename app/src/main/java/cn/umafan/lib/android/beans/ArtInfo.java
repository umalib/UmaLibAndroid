package cn.umafan.lib.android.beans;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity(createInDb = false, nameInDb = "article")
public class ArtInfo {
    @Id
    @Property(nameInDb = "id")
    private Long id;

    @Property(nameInDb = "name")
    private String name;
    @Property(nameInDb = "note")
    private String note;
    @Property(nameInDb = "author")
    private String author;
    @Property(nameInDb = "translator")
    private String translator;
    @Property(nameInDb = "uploadTime")
    private int uploadTime;
    @Property(nameInDb = "source")
    private String source;

    @ToMany(referencedJoinProperty = "artId")
    private List<Tagged> taggedList;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1125819822)
    private transient ArtInfoDao myDao;

    @Generated(hash = 505436297)
    public ArtInfo(Long id, String name, String note, String author,
                   String translator, int uploadTime, String source) {
        this.id = id;
        this.name = name;
        this.note = note;
        this.author = author;
        this.translator = translator;
        this.uploadTime = uploadTime;
        this.source = source;
    }

    @Generated(hash = 1265393630)
    public ArtInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public int getUploadTime() {
        return this.uploadTime;
    }

    public void setUploadTime(int uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1964025970)
    public List<Tagged> getTaggedList() {
        if (taggedList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TaggedDao targetDao = daoSession.getTaggedDao();
            List<Tagged> taggedListNew = targetDao._queryArtInfo_TaggedList(id);
            synchronized (this) {
                if (taggedList == null) {
                    taggedList = taggedListNew;
                }
            }
        }
        return taggedList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 300101849)
    public synchronized void resetTaggedList() {
        taggedList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 836848713)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getArtInfoDao() : null;
    }
}
