import AsyncStorage from "@react-native-async-storage/async-storage";
import {createContext, useEffect, useState} from "react";


export enum ThemesEnum {
    NGA,
    ELUI,
    CYAN,
    TEIO,
    PURPLE,
    BLACK,
    GREEN,
    EXHENTAI,
    PINK,
    PORN,
}

export const ThemeContext = createContext({
    theme: ThemesEnum.NGA,
    setTheme: (theme: ThemesEnum) => {},
});
export function useTheme() {
    const [theme, setTheme] = useState(ThemesEnum.NGA);
    const setThemeFunc = async (theme: ThemesEnum) => {
        try {
            await AsyncStorage.setItem(
                'theme',
                theme.toString(),
            );
            setTheme(theme);
        } catch (error) {
            console.error(error);
        }
    };
    const getTheme = async () => {
        try {
            const value = await AsyncStorage.getItem('theme');
            if (value !== null) {
                setTheme(parseInt(value));
            }
        } catch (error) {
            console.error(error);
        }
    };
    useEffect(() => {
       getTheme().then();
    });

    return {
        theme,
        setTheme: setThemeFunc,
    };
}