import {Text, View} from "react-native";
import {NativeStackScreenProps} from "@react-navigation/native-stack";
import {RootStackParamList} from "../../types/pages.ts";
import {useCallback, useEffect, useState} from "react";
import {getArticle} from "../../hooks/database.ts";
import {Article} from "../../types/article.ts";

type Props = NativeStackScreenProps<RootStackParamList, 'Reader'>;

export function ReaderPage({ route, navigation }: Props) {
    const [article, setArticle] = useState<Article>();
    const loadArticle = useCallback(async () => {
        const article = await getArticle(route.params?.id);
        setArticle(article);
        navigation.setOptions({ title: article.name });
    }, []);
    useEffect(() => {
        // navigation.setOptions({ title: route.params?.id });
        loadArticle().then();
    }, []);
    return (
      <View>
          <Text>Reader: {route.params?.id}</Text>
      </View>
    );
}