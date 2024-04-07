import {FlatList, Text, View} from "react-native";
import {NativeStackScreenProps} from "@react-navigation/native-stack";
import {HomeTabParamList, RootStackParamList} from "../../types/pages.ts";
import {useCallback, useContext, useEffect, useState} from "react";
import {getArticles, useDatabase} from "../../hooks/database.ts";
import {NavigationProp, useNavigation} from "@react-navigation/native";
import {ArticleItem} from "../../components/ArticleItem.tsx";
import {Article} from "../../types/article.ts";
import {useThemeColors, useThemeContext} from "../../hooks/themes.ts";
import {Button} from "react-native-paper";
import {ThemesEnum} from "../../config/themes.ts";

type Props = NativeStackScreenProps<HomeTabParamList, 'Main'>;

export function MainScreen({}: Props) {
    const [articles, setArticles] = useState<Article[]>([]);
    const [pagination, setPagination] = useState({
        current: 1,
        total: 0,
        pageSize: 10,
    });

    const loadArticles = useCallback(async () => {
        const articlesList = await getArticles(pagination.current, pagination.pageSize);
        setArticles(articlesList);
    }, []);

    useEffect(() => {
        loadArticles().then();
    }, [pagination]);

    const theme = useThemeColors();
    const nav = useNavigation<NavigationProp<RootStackParamList>>();

    return (
        <View style={{
            flex: 1,
            alignItems: 'center',
            backgroundColor: theme.primaryDeepFade,
        }}>
            <Button
                onPress={() => {
                    // nav.navigate('Reader', {id: 'from main'});
                    // theme.setTheme(ThemesEnum.NGA)
                }}
            >
                111
            </Button>
            <FlatList
                style={{
                    padding: 15,
                    width: '100%',
                }}
                data={articles}
                renderItem={
                    ({item}) => <ArticleItem info={item}/>
                }
            />
        </View>
    )
}