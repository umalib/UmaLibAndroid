import {Button, Dialog} from "react-native-paper";
import {useState} from "react";
import {FlatList, ListRenderItem, View} from "react-native";
import Icon from "react-native-vector-icons/FontAwesome";

interface PageSelectorProps {
    visible: boolean;
    page: number;
    totalPages: number;
    groupSize?: number;
    onSelect?: (page: number) => void;
    onCancel?: () => void;
}

export function PageSelector(
    {
        visible = false,
        page = 1,
        totalPages = 1,
        onSelect,
        onCancel,
        groupSize = 100,
    }: PageSelectorProps
) {
    const [selectedPage, setSelectedPage] = useState(page);
    const [isGroup, setIsGroup] = useState(true);
    const [pageConfig, setPageConfig] = useState({
        start: 0,
        end: 0,
    });

    const groupRender: ListRenderItem<number> = ({item: group}) => {
        const startPage = group * groupSize + 1;
        const endPage = Math.min((group + 1) * groupSize, totalPages);
        return (
            <Button
                style={{
                    margin: 5,
                }}
                mode={selectedPage >= startPage && selectedPage <= endPage ? 'contained' : 'outlined'}
                key={group}
                onPress={() => {
                    setPageConfig({
                        start: startPage,
                        end: endPage,
                    });
                    setIsGroup(false);
                }}
            >
                {startPage} - {endPage}页
            </Button>
        );
    };
    const pageRender: ListRenderItem<number> = ({item: page}) => {
        const currentPage = pageConfig.start + page;
        return (
            <View
                style={{
                    padding: 5,
                    width: '33.3%',
                }}
            >
                <Button
                    style={{
                        padding: 0,
                    }}
                    mode={selectedPage === currentPage ? 'contained' : 'outlined'}
                    key={page}
                    onPress={() => {
                        setSelectedPage(currentPage);
                    }}
                >
                    {currentPage}
                </Button>
            </View>
        );
    };

    return visible ? (
        <View
            style={{
                position: 'absolute',
                width: '100%',
                height: '100%',
                zIndex: 100,
            }}
        >
            <Dialog
                style={{
                    maxHeight: '80%',
                }}
                visible={visible}
                onDismiss={onCancel}
            >
                <Dialog.Title>选择页码</Dialog.Title>
                <Dialog.ScrollArea>
                    {
                        isGroup ? (
                            <FlatList
                                data={[...(new Array(Math.ceil(totalPages / groupSize)).keys())]}
                                renderItem={groupRender}
                            />
                        ) : (
                            <>
                                <Button
                                    style={{
                                        margin: 5,
                                    }}
                                    mode="outlined"
                                    onPress={() => {
                                        setIsGroup(true);
                                    }}
                                >
                                    <Icon name="reply" />
                                </Button>
                                <FlatList
                                    numColumns={3}
                                    data={[...(new Array(Math.ceil(pageConfig.end - pageConfig.start + 1)).keys())]}
                                    renderItem={pageRender}
                                />
                            </>
                        )
                    }
                </Dialog.ScrollArea>
                <Dialog.Actions>
                    <Button onPress={() => onCancel?.()}>取消</Button>
                    <Button onPress={() => onSelect?.(selectedPage)}>确认</Button>
                </Dialog.Actions>
            </Dialog>
        </View>
    ) : (<></>);
}