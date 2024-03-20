import {Text, View} from "react-native";
import {HomeTabParamList} from "../../types/pages.ts";
import {NativeStackScreenProps} from "@react-navigation/native-stack";

type Props = NativeStackScreenProps<HomeTabParamList, 'Setting'>;

export function SettingScreen({ navigation, route }: Props) {
    return (
        <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
            <Text>Setting Screen</Text>
        </View>
    );
}