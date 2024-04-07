import {enablePromise, openDatabase, SQLiteDatabase} from "react-native-sqlite-storage";
import {Article} from "../types/article.ts";

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

export const getArticles = async (current: number, pageSize: number): Promise<Article[]> => {
    const db = await useDatabase();
    try {
        const items: Article[] = [];
        const results = await db.executeSql(`
            SELECT 
            id, name, note, content, author, translator, source ,uploadTime 
            FROM article 
            ORDER BY uploadTime DESC
            LIMIT ${pageSize} OFFSET ${(current - 1) * pageSize}
        `);
        results.forEach(result => {
            for (let index = 0; index < result.rows.length; index++) {
                items.push(result.rows.item(index))
            }
        });
        return items;
    } catch (error) {
        console.error(error);
        throw Error('Failed to get Articles !!!');
    }
};

export const getArticle = async (id: number): Promise<Article> => {
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

