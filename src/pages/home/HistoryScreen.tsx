import {Text, View} from "react-native";
import {NativeStackScreenProps} from "@react-navigation/native-stack";
import {HomeTabParamList} from "../../types/pages.ts";

type Props = NativeStackScreenProps<HomeTabParamList, 'History'>

export function HistoryScreen({ navigation, route }: Props) {
    return (
        <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
            <Text>History Screen</Text>
        </View>
    );
}