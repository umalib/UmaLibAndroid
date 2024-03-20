import {Button, Text, View} from "react-native";
import {NativeStackScreenProps} from "@react-navigation/native-stack";
import {HomeTabParamList} from "../../types/pages.ts";

type Props = NativeStackScreenProps<HomeTabParamList, 'Collection'>;

export function CollectionPage({ navigation, route }: Props) {
    return (
        <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
            <Text>Collection: {route.params?.id}</Text>
            <Button
                title="back to main"
                onPress={() => {
                    navigation.navigate('Main', {id: 'from collection'});
                }}
            />
        </View>
    );
}