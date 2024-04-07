import AsyncStorage from "@react-native-async-storage/async-storage";
import {createContext, useContext, useEffect, useState} from "react";
import {ThemesEnum, THEME_TITLES, THEMES_BASE_COLORS} from "../config/themes.ts";
import {generateColors} from "../utils/color.ts";

export const ThemeContext = createContext({
    theme: ThemesEnum.NGA,
    colors: generateColors(useThemeBaseColors(ThemesEnum.NGA)),
    setTheme: (theme: ThemesEnum) => {},
});

/**
 * 通用hook方法，获取响应式主题类型和setter
 */
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
    }, []);

    return {
        theme,
        colors: generateColors(useThemeBaseColors(theme)),
        setTheme: setThemeFunc,
    };
}

export function useThemeContext() {
    return useContext(ThemeContext);
}

export function useThemeType() {
    const context = useContext(ThemeContext);
    return context.theme;
}

export function useThemeColors() {
    const context = useContext(ThemeContext);
    return context.colors;
}

export function useThemeTitle(theme: ThemesEnum) {
    return THEME_TITLES[theme];
}

export function useThemeBaseColors(theme: ThemesEnum) {
    return THEMES_BASE_COLORS[theme];
}