import {enablePromise, openDatabase, SQLiteDatabase} from "react-native-sqlite-storage";
import {Article, QueryOptions, Tag} from "../types/article.ts";

enablePromise(true);

let db: SQLiteDatabase | null = null;
export async function useDatabase() {
    if (db) {
        return db;
    }
    db = await openDatabase({
        name: 'main.db',
        createFromLocation: 1,
    });
    return db;
}

/**
 * 获取单篇文章的所有Tag
 * @param target
 */
export async function getArticleTags(target: number | Article) {
    const db = await useDatabase();
    try {
        const articleId = typeof target === 'number' ? target : target.id;
        const results = await db.executeSql(`
            SELECT 
            tag.id, tag.name, tag.type, tag.cover, tag.description
            FROM tagged
            JOIN tag
            ON tag.id = tagged.tagId
            WHERE tagged.artId = ${articleId}
        `);
        const items: Tag[] = [];
        results.forEach(result => {
            for (let index = 0; index < result.rows.length; index++) {
                items.push(result.rows.item(index));
            }
        });
        return items;
    }
    catch (error) {
        console.error(error);
        throw Error('Failed to get Article Tags !!!');
    }
}

/**
 * 获取所有文章
 * @param current
 * @param pageSize
 * @param query
 */
export async function getArticles(current: number, pageSize: number, query: QueryOptions = {}): Promise<Article[]> {
    const db = await useDatabase();
    try {
        const items: Article[] = [];
    //     获取文章及其相关tag
    //     SELECT
    //     a.*,
    //         GROUP_CONCAT('{ "id":"' || tag.id || '","name":"' || tag.name || '","type":' || tag.type || '}', ';') as tags
    //     FROM (
    //         SELECT * FROM article
    //     ORDER BY uploadTime DESC
    //     LIMIT 10 OFFSET 0
    // ) a
    //     JOIN tagged ON tagged.artId = a.id
    //     JOIN tag ON tagged.tagId = tag.id
    //     GROUP BY a.id
    //     ORDER BY a.id DESC
        const results = await db.executeSql(`
            SELECT 
            a.*,
            GROUP_CONCAT(tag.id || '||' || tag.name || '||' || tag.type , '+;') as tags
            FROM (
                SELECT
                id, name, note, author, translator, source, uploadTime 
                FROM article
                ORDER BY uploadTime DESC
                LIMIT ${pageSize} OFFSET ${(current - 1) * pageSize}
            ) a
            JOIN tagged ON tagged.artId = a.id
            JOIN tag ON tagged.tagId = tag.id
            GROUP BY a.id
            ORDER BY a.id DESC
        `);
        results.forEach(result => {
            for (let index = 0; index < result.rows.length; index++) {
                const article = result.rows.item(index);
                article.tagList = article.tags.split('+;').map((tag: string) => {
                    const [id, name, type] = tag.split('||');
                    return {id: parseInt(id), name, type: parseInt(type)};
                });
                items.push(result.rows.item(index));
            }
        });
        return items;
    } catch (error) {
        console.error(error);
        throw Error('Failed to get Articles !!!');
    }
}

export async function getArticle(id: number): Promise<Article> {
    const db = await useDatabase();
    try {
        const results = await db.executeSql(`
            SELECT 
            id, name, note, content, author, translator, source ,uploadTime 
            FROM article 
            WHERE id = ${id}
        `);
        const data = results.length && results[0].rows.length && results[0].rows.item(0);
        if (data) {
            return data;
        }
        throw Error('Article not found !!!');
    } catch (error) {
        console.error(error);
        throw Error('Failed to get Article !!!');
    }
}

export async function getArticleCount() {
    const db = await useDatabase();
    try {
        const results = await db.executeSql(`
            SELECT COUNT(*) as count FROM article
        `);
        const data = results.length && results[0].rows.length && results[0].rows.item(0);
        if (data) {
            return data.count;
        }
        throw Error('Article not found !!!');
    } catch (error) {
        console.error(error);
        throw Error('Failed to get Article\'s Count !!!');
    }
}

