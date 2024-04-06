
export function rgb2hsl(rgb: string) {
    const r = parseInt(rgb.slice(1, 3), 16) / 255;
    const g = parseInt(rgb.slice(3, 5), 16) / 255;
    const b = parseInt(rgb.slice(5, 7), 16) / 255;

    const max = Math.max(r, g, b);
    const min = Math.min(r, g, b);
    const l = (max + min) / 2;
    let h = 0;
    let s = 0;
    if (max !== min) {
        const d = max - min;
        s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
        switch (max) {
            case r:
                h = (g - b) / d + (g < b ? 6 : 0);
                break;
            case g:
                h = (b - r) / d + 2;
                break;
            case b:
                h = (r - g) / d + 4;
                break;
        }
        h /= 6;
    }

    return {
        h: h * 360,
        s: s * 100,
        l: l * 100,
    };
}

function transferColor(hsl: {h: number, s: number, l: number}, transfer?: {h?: number, s?: number, l?: number}) {
    const result = transfer ? {
        h: hsl.h * (transfer.h || 1),
        s: hsl.s * (transfer.s || 1),
        l: hsl.l * (transfer.l || 1),
    } : { ...hsl };
    return `hsl(${result.h}, ${result.s}%, ${result.l}%)`;
}

export function generateColors(colors: string[]) {
    const primaryOrigin = rgb2hsl(colors[0]);
    const secondaryOrigin = rgb2hsl(colors[1]);
    console.log(transferColor(primaryOrigin));
    return {
        primary: transferColor(primaryOrigin),
        secondary: transferColor(secondaryOrigin),
        primaryLight: transferColor(primaryOrigin, { l: 1.1 }),
        secondaryLight: transferColor(secondaryOrigin, { l: 1.1 }),
        primaryDark: transferColor(primaryOrigin, { l: 0.9 }),
        secondaryDark: transferColor(secondaryOrigin, { l: 0.9 }),
        primaryDeepLight: transferColor(primaryOrigin, { l: 1.2 }),
        secondaryDeepLight: transferColor(secondaryOrigin, { l: 1.2 }),
        primaryDeepDark: transferColor(primaryOrigin, { l: 0.8 }),
        secondaryDeepDark: transferColor(secondaryOrigin, { l: 0.8 }),
        primaryFade: transferColor(primaryOrigin, { s: 0.8 }),
    };
}