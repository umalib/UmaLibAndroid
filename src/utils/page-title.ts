import {ThemesEnum} from './themes.ts';

const TITLES = {
    [ThemesEnum.NGA]: '那我呢',
    [ThemesEnum.ELUI]: '特别白',
    [ThemesEnum.CYAN]: '铃鹿青',
    [ThemesEnum.TEIO]: '帝王蓝',
    [ThemesEnum.PURPLE]: '麦昆紫',
    [ThemesEnum.BLACK]: '玄驹黑',
    [ThemesEnum.GREEN]: '光钻绿',
    [ThemesEnum.EXHENTAI]: '荒漠灰',
    [ThemesEnum.PINK]: '飞鹰粉',
    [ThemesEnum.PORN]: '气槽黄',
};

export default function getPageTitle(theme: ThemesEnum) {
    return TITLES[theme];
}