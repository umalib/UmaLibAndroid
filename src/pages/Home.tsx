import {NativeStackScreenProps} from "@react-navigation/native-stack";
import {HomeTabParamList, RootStackParamList} from "../types/pages.ts";
import {MainScreen} from "./home/MainScreen.tsx";
import Icon from "react-native-vector-icons/FontAwesome";
import {CollectionPage} from "./home/CollectionScreen.tsx";
import React from "react";
import { createMaterialBottomTabNavigator } from 'react-native-paper/react-navigation';
import {HistoryScreen} from "./home/HistoryScreen.tsx";
import {SettingScreen} from "./home/SettingScreen.tsx";

type Props = NativeStackScreenProps<RootStackParamList, 'Home'>;
const HomeTab = createMaterialBottomTabNavigator<HomeTabParamList>();


const IconMap = {
    Main: 'home',
    Collection: 'bookmark',
    History: 'history',
    Setting: 'cog',
};

export function HomePage({navigation, route}: Props) {
    return (
        <HomeTab.Navigator
            initialRouteName="Main"
            screenOptions={({route}) => ({
                headerStyle: {
                    backgroundColor: '#f4511e',
                },
                headerTintColor: '#fff',
                headerTitleStyle: {
                    fontWeight: 'bold',
                },
                tabBarIcon: ({color}) => {
                    return <Icon name={IconMap[route.name]} color={color} size={20}/>;
                },
            })}
            shifting={true}
        >
            <HomeTab.Screen
                name="Main"
                component={MainScreen}
                options={{
                    tabBarLabel: '主页',
                }}
            />
            <HomeTab.Screen
                name="Collection"
                component={CollectionPage}
                options={{
                    tabBarLabel: '收藏',
                }}
            />
            <HomeTab.Screen
                name="History"
                component={HistoryScreen}
                options={{
                    tabBarLabel: '历史',
                }}
            />
            <HomeTab.Screen
                name="Setting"
                component={SettingScreen}
                options={{
                    tabBarLabel: '设置',
                }}
            />
        </HomeTab.Navigator>
    );
}