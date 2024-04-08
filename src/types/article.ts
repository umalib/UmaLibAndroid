export interface Article {
    id: number;
    name: string;
    note: string;
    content: string;
    author: string;
    translator: string;
    uploadTime: number;
    source: string;
    tagList?: Tag[];
}

export interface Tagged {
    id: number;
    artId: number;
    tagId: number;
    article?: Article;
    tag?: Tag;
}

export interface Tag {
    id: number;
    name: string;
    type: number;
    cover?: string;
    description?: string;
    taggedList?: Tagged[];
}

export interface QueryOptions {
    creators?: string[];
    keyword?: string;
    tags?: string[];
    excludeTags?: string[];
}