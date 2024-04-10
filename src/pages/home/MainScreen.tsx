import {FlatList, View} from "react-native";
import {NativeStackScreenProps} from "@react-navigation/native-stack";
import {HomeTabParamList} from "../../types/pages.ts";
import {useCallback, useEffect, useState} from "react";
import {getArticleCount, getArticles} from "../../hooks/database.ts";
import {ArticleItem} from "../../components/ArticleItem.tsx";
import {Article} from "../../types/article.ts";
import {useThemeColors, useThemeContext} from "../../hooks/themes.ts";
import {Banner, Button, IconButton, Text} from "react-native-paper";

type Props = NativeStackScreenProps<HomeTabParamList, 'Main'>;

export function MainScreen({}: Props) {
    const [articles, setArticles] = useState<Article[]>([]);
    const [total, setTotal] = useState(0);
    const [searchVisible, setSearchVisible] = useState(false);
    const [pagination, setPagination] = useState({
        current: 1,
        pageSize: 10,
    });

    const loadArticles = useCallback(async () => {
        const articlesList = await getArticles(pagination.current, pagination.pageSize);
        setArticles(articlesList);
    }, []);
    const loadTotal = useCallback(async () => {
        const total = await getArticleCount();
        setTotal(total);
    }, []);

    useEffect(() => {
        loadArticles().then();
    }, [pagination]);
    useEffect(() => {
        loadTotal().then();
    }, []);

    const theme = useThemeColors();

    return (
        <View style={{
            flex: 1,
            alignItems: 'center',
            backgroundColor: theme.primaryDeepFade,
        }}>
            <FlatList
                style={{
                    paddingHorizontal: 15,
                    width: '100%',
                }}
                data={articles}
                renderItem={
                    ({item}) => <ArticleItem info={item}/>
                }
            />

            {/* 搜索按钮 */}
            <IconButton
                style={{
                    position: 'absolute',
                    right: 10,
                    bottom: 80,
                }}
                size={30}
                mode="contained"
                icon="magnify"
                iconColor={theme.primaryLight}
                containerColor={theme.secondaryDeepFade}
                onPress={() => setSearchVisible(!searchVisible)}
            />

            <View
                style={{
                    position: 'absolute',
                    display: 'flex',
                    flexDirection: 'row',
                    right: 10,
                    bottom: 20,
                    backgroundColor: theme.secondaryDeepFade,
                }}
            >
                <IconButton
                    icon="menu-left"
                    disabled={pagination.current <= 1}
                    onPress={() => {
                        if (pagination.current > 1) {
                            setPagination({
                                ...pagination,
                                current: pagination.current - 1,
                            });
                        }
                    }}
                />
                <Text>{pagination.current}/{total}</Text>
                <IconButton
                    icon="menu-right"
                    disabled={pagination.current >= total}
                    onPress={() => {
                        if (pagination.current < total) {
                            setPagination({
                                ...pagination,
                                current: pagination.current + 1,
                            });
                        }
                    }}
                />
            </View>
        </View>
    )
}