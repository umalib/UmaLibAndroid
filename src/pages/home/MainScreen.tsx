import {Text, View} from "react-native";
import {NativeStackScreenProps} from "@react-navigation/native-stack";
import {HomeTabParamList} from "../../types/pages.ts";

type Props = NativeStackScreenProps<HomeTabParamList, 'Main'>;

export function MainScreen({ navigation, route }: Props) {
    return (
        <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
            <Text>Main Screen</Text>
        </View>
    )
}