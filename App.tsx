import React from 'react';
import {
    SafeAreaView,
} from 'react-native';

import {HomePage} from "./src/pages/Home";
import {NavigationContainer} from "@react-navigation/native";
import {RootStackParamList} from "./src/types/pages.ts";
import {createNativeStackNavigator} from "@react-navigation/native-stack";
import {ReaderPage} from "./src/pages/common/ReaderPage.tsx";
import {ThemeContext, useMaterialColors, useTheme} from "./src/hooks/themes.ts";
import {DefaultTheme, PaperProvider} from "react-native-paper";
import Toast from "react-native-toast-message";

const Stack = createNativeStackNavigator<RootStackParamList>();

function App(): React.JSX.Element {
    const theme = useTheme();
    const themeMaterialColors = useMaterialColors();

    const materialTheme = {
        ...DefaultTheme,
        // Specify custom property
        myOwnProperty: true,
        // Specify custom property in nested object
        colors: {
            ...DefaultTheme.colors,
            // ...theme.colors,
            ...themeMaterialColors,
        },
    };
    return (
        <>
            <PaperProvider theme={materialTheme}>
                <ThemeContext.Provider value={theme}>
                    <NavigationContainer>
                        <SafeAreaView style={{
                            flex: 1,
                        }}>
                            <Stack.Navigator
                                initialRouteName="Home"
                                screenOptions={{
                                    headerStyle: {
                                        backgroundColor: theme.colors.secondaryDeepLight,
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
                                    options={{headerShown: false}}
                                />
                                <Stack.Screen
                                    name="Reader"
                                    component={ReaderPage}
                                />
                            </Stack.Navigator>
                        </SafeAreaView>
                    </NavigationContainer>
                </ThemeContext.Provider>
            </PaperProvider>
            <Toast />
        </>

    );
}

export default App;
