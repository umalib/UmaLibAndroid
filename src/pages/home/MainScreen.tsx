import {Button, FlatList, Text, View} from "react-native";
import {NativeStackScreenProps} from "@react-navigation/native-stack";
import {HomeTabParamList, RootStackParamList} from "../../types/pages.ts";
import {useCallback, useContext, useEffect, useState} from "react";
import {getArticles, useDatabase} from "../../hooks/database.ts";
import {NavigationProp, useNavigation} from "@react-navigation/native";
import {ArticleItem} from "../../components/ArticleItem.tsx";
import {Article} from "../../types/article.ts";
import {styles} from "../../style/main.ts";
import {ThemeContext} from "../../utils/themes.ts";

type Props = NativeStackScreenProps<HomeTabParamList, 'Main'>;

export function MainScreen({ }: Props) {
    const [articles, setArticles] = useState<Article[]>([]);
    const main = useCallback(async () => {
        try {
            const db = await useDatabase();
            const articlesList = await getArticles(db, 1, 10);
            setArticles(articlesList);
        } catch (error) {
            console.error('error', error);
        }
    }, []);
    useEffect(() => {
        main();
    }, []);
    const theme = useContext(ThemeContext);
    const nav = useNavigation<NavigationProp<RootStackParamList>>();
    return (
        <View style={{ flex: 1, alignItems: 'center' }}>
            <Text>Main Screen{theme.theme}</Text>
            <Button
                title={"test"}
                onPress={() => {
                    nav.navigate('Reader', {id: 'from main'});
                }}
            />
            <FlatList
                style={styles.list}
                data={articles}
                renderItem={({ item }) => <ArticleItem info={item}/>}
            />
        </View>
    )
}