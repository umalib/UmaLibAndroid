import {enablePromise, openDatabase, SQLiteDatabase} from "react-native-sqlite-storage";
import {Article} from "../types/article.ts";

enablePromise(true);

export async function useDatabase() {
    return openDatabase({
        name: 'main.db',
        createFromLocation: 1,
    });
}

export const getArticles = async (db: SQLiteDatabase): Promise<Article[]> => {
    try {
        const items: Article[] = [];
        const results = await db.executeSql(`SELECT id, name FROM article`);
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

