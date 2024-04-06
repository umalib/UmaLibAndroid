import {Article} from "../types/article.ts";
import {Card, Button, Text, IconButton} from "react-native-paper";
import dayjs from "dayjs";
import {useThemeColors} from "../hooks/themes.ts";

interface ArticleItemProps {
    info: Article;
}

export function ArticleItem({ info }: ArticleItemProps) {
    const themeColors = useThemeColors();
    return (
        <Card
            accessible={true}
            style={{
                backgroundColor: themeColors.primary,
                marginTop: 10,
                marginHorizontal: 5,
            }}
            onPress={() => {}}
        >
            <Card.Title
                title={info.name}
                titleVariant="titleMedium"
                subtitle={
                    `上传时间：${dayjs(info.uploadTime * 1000).format('YYYY-MM-DD')}`
                }
            />
            <Card.Content>
                <Text variant="titleLarge">Card title</Text>
                <Text variant="bodyMedium">Card content</Text>
            </Card.Content>
            <Card.Actions>
                <IconButton
                    icon="star-outline"
                    onPress={() => {}}
                />
                <IconButton
                    icon="book-open-variant"
                    onPress={() => {}}
                    iconColor={themeColors.primary}
                    containerColor={themeColors.secondary}
                />
            </Card.Actions>
        </Card>
    );
}