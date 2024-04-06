import {FlatList, Text, View} from "react-native";
import {NativeStackScreenProps} from "@react-navigation/native-stack";
import {HomeTabParamList, RootStackParamList} from "../../types/pages.ts";
import {useCallback, useContext, useEffect, useState} from "react";
import {getArticles, useDatabase} from "../../hooks/database.ts";
import {NavigationProp, useNavigation} from "@react-navigation/native";
import {ArticleItem} from "../../components/ArticleItem.tsx";
import {Article} from "../../types/article.ts";
import {ThemeContext, useThemeColors, useThemeTitle} from "../../hooks/themes.ts";
import {ThemesEnum} from "../../config/themes.ts";
import {Button} from "react-native-paper";

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
    const theme = useThemeColors();
    const nav = useNavigation<NavigationProp<RootStackParamList>>();
    return (
        <View style={{
            flex: 1,
            alignItems: 'center',
            backgroundColor: theme.primaryLight,
        }}>
            <Text>Main Screen</Text>
            <Button
                onPress={() => {
                    // nav.navigate('Reader', {id: 'from main'});
                    // theme.setTheme(ThemesEnum.NGA)
                }}
            >111</Button>
            <FlatList
                style={{
                    padding: 15,
                    width: '100%',
                }}
                data={articles}
                renderItem={({ item }) => <ArticleItem info={item}/>}
            />
        </View>
    )
}