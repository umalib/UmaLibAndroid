import {Article} from "../types/article.ts";
import {Card, Button, Text, IconButton, Divider, Snackbar, Chip} from "react-native-paper";
import dayjs from "dayjs";
import {useMaterialColors, useThemeColors} from "../hooks/themes.ts";
import {NavigationProp, useNavigation} from "@react-navigation/native";
import {RootStackParamList} from "../types/pages.ts";
import {View} from "react-native";
import {useState} from "react";

interface ArticleItemProps {
    info: Article;
}

export function ArticleItem({info}: ArticleItemProps) {
    const themeColors = useThemeColors();
    const nav = useNavigation<NavigationProp<RootStackParamList>>();

    const [visible, setVisible] = useState(false);
    const onToggleSnackBar = () => setVisible(!visible);
    const onDismissSnackBar = () => setVisible(false);

    const goToReader = () => {
        nav.navigate('Reader', {id: info.id});
    };

    return (
        <>
            <Card
                accessible={true}
                style={{
                    backgroundColor: themeColors.primary,
                    marginVertical: 5,
                    marginHorizontal: 5,
                }}
                onPress={() => {
                    onToggleSnackBar()
                }}
            >
                <Card.Title
                    title={info.name}
                    titleVariant="titleMedium"
                    style={{
                        marginBottom: -15,
                    }}
                />
                <Card.Content>
                    <View
                        style={{
                            flexDirection: 'row',
                            justifyContent: 'space-between',
                        }}
                    >
                        <Text
                            numberOfLines={1}
                            style={{
                                maxWidth: '70%',
                            }}
                            variant="bodyMedium">
                            译者：{info.translator}
                        </Text>
                        <Text
                            numberOfLines={1}
                            variant="bodyMedium"
                            style={{
                                maxWidth: '30%',
                                color: themeColors.fade,
                            }}
                        >
                            {info.author}
                        </Text>
                    </View>
                    <Divider style={{
                        marginVertical: 5,
                    }}/>
                    <Text variant="bodyMedium">{info.note}</Text>
                    <Divider style={{
                        marginTop: 5,
                    }}/>
                    <View style={{
                        flexDirection: 'row',
                        flexWrap: 'wrap',
                        gap: 2,
                        marginTop: 5,
                    }}>
                        {
                            info?.tagList?.map((tag, index) => (
                                <Chip
                                    key={tag.id}
                                    compact={true}
                                    textStyle={{
                                        marginVertical: 0,
                                        color: themeColors.primary,
                                        fontSize: 10,
                                    }}
                                    style={{
                                        backgroundColor: themeColors.secondary,
                                        height: 20,
                                        justifyContent: 'center',
                                        padding: 0,
                                        margin: 0,
                                        display: 'flex',
                                        alignItems: 'center',
                                    }}
                                    onPress={() => {
                                    }}
                                >
                                    {tag.name}
                                </Chip>
                            ))
                        }
                    </View>
                </Card.Content>
                <Card.Actions>
                    <Text
                        style={{
                            width: '100%',
                            flex: 1,
                            color: themeColors.secondary,
                        }}
                    >
                        {dayjs(info.uploadTime * 1000).format('YYYY-MM-DD HH:mm')}
                    </Text>
                    <IconButton
                        icon="star-outline"
                        iconColor={themeColors.secondary}
                        containerColor={themeColors.primaryDark}
                        onPress={() => {
                        }}
                    />
                    <IconButton
                        icon="book-open-variant"
                        iconColor={themeColors.primary}
                        containerColor={themeColors.secondary}
                        onPress={goToReader}
                    />
                </Card.Actions>
            </Card>
            <Snackbar
                visible={visible}
                onDismiss={onDismissSnackBar}
                action={{
                    label: '关闭',
                }}
                style={{
                    backgroundColor: themeColors.primaryDark,
                }}
            >
                <Text>标题：{info.name}</Text>
                <Text>作者：{info.author}</Text>
                <Text>译者：{info.translator}</Text>
            </Snackbar>
        </>

    );
}