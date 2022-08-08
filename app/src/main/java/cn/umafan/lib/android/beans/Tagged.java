package cn.umafan.lib.android.beans;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;

@Entity(createInDb = false)
public class Tagged {
    @Id
    @Property(nameInDb = "id")
    private Long id;

    @Property(nameInDb = "artId")
    private Long artId;
    @Property(nameInDb = "tagId")
    private Long tagId;

    @ToOne(joinProperty = "artId")
    private Article article;
    @ToOne(joinProperty = "tagId")
    private Tag tag;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1937579100)
    private transient TaggedDao myDao;
    @Generated(hash = 341632769)
    private transient Long article__resolvedKey;
    @Generated(hash = 1006483784)
    private transient Long tag__resolvedKey;

    @Generated(hash = 512951215)
    public Tagged(Long id, Long artId, Long tagId) {
        this.id = id;
        this.artId = artId;
        this.tagId = tagId;
    }

    @Generated(hash = 1923984463)
    public Tagged() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArtId() {
        return this.artId;
    }

    public void setArtId(Long artId) {
        this.artId = artId;
    }

    public Long getTagId() {
        return this.tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 153071970)
    public Article getArticle() {
        Long __key = this.artId;
        if (article__resolvedKey == null || !article__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ArticleDao targetDao = daoSession.getArticleDao();
            Article articleNew = targetDao.load(__key);
            synchronized (this) {
                article = articleNew;
                article__resolvedKey = __key;
            }
        }
        return article;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 2134606738)
    public void setArticle(Article article) {
        synchronized (this) {
            this.article = article;
            artId = article == null ? null : article.getId();
            article__resolvedKey = artId;
        }
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 891466911)
    public Tag getTag() {
        Long __key = this.tagId;
        if (tag__resolvedKey == null || !tag__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TagDao targetDao = daoSession.getTagDao();
            Tag tagNew = targetDao.load(__key);
            synchronized (this) {
                tag = tagNew;
                tag__resolvedKey = __key;
            }
        }
        return tag;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 106711820)
    public void setTag(Tag tag) {
        synchronized (this) {
            this.tag = tag;
            tagId = tag == null ? null : tag.getId();
            tag__resolvedKey = tagId;
        }
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
    @Generated(hash = 1785699045)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTaggedDao() : null;
    }
}
