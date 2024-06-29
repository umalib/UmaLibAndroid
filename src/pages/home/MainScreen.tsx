import {FlatList, View} from "react-native";
import {NativeStackScreenProps} from "@react-navigation/native-stack";
import {HomeTabParamList} from "../../types/pages.ts";
import {useCallback, useEffect, useRef, useState} from "react";
import {getArticleCount, getArticles} from "../../hooks/database.ts";
import {ArticleItem} from "../../components/ArticleItem.tsx";
import {Article} from "../../types/article.ts";
import {useThemeColors, useThemeContext} from "../../hooks/themes.ts";
import {ActivityIndicator, Banner, Button, IconButton, Text} from "react-native-paper";
import {PageSelector} from "../../components/PageSelector.tsx";

type Props = NativeStackScreenProps<HomeTabParamList, 'Main'>;

export function MainScreen({}: Props) {
    const [isLoading, setIsLoading] = useState(false);
    const [articles, setArticles] = useState<Article[]>([]);
    const [total, setTotal] = useState(0);
    const [searchVisible, setSearchVisible] = useState(false);
    const [pagination, setPagination] = useState({
        current: 1,
        pageSize: 10,
    });
    const [pageSelectorVisible, setPageSelectorVisible] = useState(false);
    const pageTotal = Math.ceil(total/pagination.pageSize);

    const flatListRef = useRef<FlatList<Article>>(null);

    const loadArticles = useCallback(async () => {
        setIsLoading(true);
        const articlesList = await getArticles(pagination.current, pagination.pageSize);
        setIsLoading(false);
        flatListRef.current?.scrollToOffset({offset: 0});
        setArticles(articlesList);
    }, [pagination]);
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

            <PageSelector
                visible={pageSelectorVisible}
                page={pagination.current}
                totalPages={pageTotal}
                onSelect={(page) => {
                    setPagination({
                        ...pagination,
                        current: page,
                    });
                    setPageSelectorVisible(false);
                }}
                onCancel={() => {
                    setPageSelectorVisible(false);
                }}
            />


            <View style={{
                position: 'absolute',
                display: isLoading ? 'flex' : 'none',
                justifyContent: 'center',
                alignItems: 'center',
                zIndex: 1,
                width: '100%',
                height: '100%',
                backgroundColor: 'rgba(0, 0, 0, 0.5)',
            }}>
                <ActivityIndicator
                    animating={true}
                    color={theme.primaryLight}
                    size="large"
                />
            </View>
            <FlatList
                ref={flatListRef}
                style={{
                    paddingHorizontal: 15,
                    width: '100%',
                }}
                data={articles}
                renderItem={
                    ({item}) => <ArticleItem info={item}/>
                }
                ListFooterComponent={() => (
                    <View style={{
                        height: 120,
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                    }}>
                        <Text style={{
                            color: theme.secondary,
                        }}>已经到底了~</Text>
                    </View>
                )}
            />

            {/* 搜索按钮 */}
            <IconButton
                style={{
                    position: 'absolute',
                    right: 10,
                    bottom: 80,
                    zIndex: 2,
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
                    borderRadius: 20,
                    alignItems: 'center',
                    zIndex: 2,
                }}
            >
                <IconButton
                    icon="menu-left"
                    iconColor={theme.primaryLight}
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
                <Text
                    style={{
                        padding: 5,
                        color: theme.primaryLight,
                    }}
                    onPress={() => setPageSelectorVisible(true)}
                >{pagination.current}/{pageTotal}</Text>
                <IconButton
                    icon="menu-right"
                    iconColor={theme.primaryLight}
                    disabled={pagination.current >= pageTotal}
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