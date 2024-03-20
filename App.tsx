import React from 'react';
import {
    SafeAreaView,
} from 'react-native';

import {HomePage} from "./src/pages/Home";
import {NavigationContainer} from "@react-navigation/native";
import {RootStackParamList} from "./src/types/pages.ts";
import {createNativeStackNavigator} from "@react-navigation/native-stack";
import {ReaderPage} from "./src/pages/common/ReaderPage.tsx";

const Stack = createNativeStackNavigator<RootStackParamList>();

function App(): React.JSX.Element {
    return (
        <NavigationContainer>
            <SafeAreaView style={{flex: 1}}>
                <Stack.Navigator
                    initialRouteName="Home"
                    screenOptions={{
                        headerStyle: {
                            backgroundColor: '#f4511e',
                        },
                        headerTintColor: '#fff',
                        headerTitleStyle: {
                            fontWeight: 'bold',
                        },
                    }}
                >
                    <Stack.Screen
                        name="Home"
                        component={HomePage}
                        options={{ headerShown: false }}
                    />
                    <Stack.Screen
                        name="Reader"
                        component={ReaderPage}
                    />
                </Stack.Navigator>
            </SafeAreaView>
        </NavigationContainer>
    );
}

export default App;
