import {Text, View} from "react-native";
import {NativeStackScreenProps} from "@react-navigation/native-stack";
import {HomeTabParamList} from "../../types/pages.ts";
import {useCallback, useEffect, useState} from "react";
import {getArticles, useDatabase} from "../../hooks/database.ts";

type Props = NativeStackScreenProps<HomeTabParamList, 'Main'>;

export function MainScreen({ navigation, route }: Props) {
    const main = useCallback(async () => {
        try {
            console.error('error');
            const db = await useDatabase();
            console.log('db', db);
            const articlesList = await getArticles(db);
            console.log('article', articlesList);
        } catch (error) {
            console.error('error', error);
        }
    }, []);
    useEffect(() => {
        main();
    }, []);
    return (
        <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
            <Text>Main Screen</Text>
        </View>
    )
}