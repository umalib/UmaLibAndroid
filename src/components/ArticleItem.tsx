import {View} from "react-native";
import {Article} from "../types/article.ts";
import {Card, Button, Text} from "react-native-paper";

interface ArticleItemProps {
    info: Article;
}

export function ArticleItem({ info }: ArticleItemProps) {

    return (
        <Card>
            <Card.Title title="Card Title" subtitle="Card Subtitle" />
            <Card.Content>
                <Text variant="titleLarge">Card title</Text>
                <Text variant="bodyMedium">Card content</Text>
            </Card.Content>
            <Card.Actions>
                <Button onPress={() => {}}>Cancel</Button>
                <Button onPress={() => {}}>Ok</Button>
            </Card.Actions>
        </Card>
    );
}