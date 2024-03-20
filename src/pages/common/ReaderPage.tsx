import {Text, View} from "react-native";
import {NativeStackScreenProps} from "@react-navigation/native-stack";
import {RootStackParamList} from "../../types/pages.ts";
import {useEffect} from "react";

type Props = NativeStackScreenProps<RootStackParamList, 'Reader'>;

export function ReaderPage({ route, navigation }: Props) {
    useEffect(() => {
        navigation.setOptions({ title: route.params?.id });
    }, []);
    return (
      <View>
          <Text>Reader: {route.params?.id}</Text>
      </View>
    );
}