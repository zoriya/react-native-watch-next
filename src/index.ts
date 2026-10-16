import { Platform } from "react-native";
import { NitroModules } from "react-native-nitro-modules";
import type {
	ProgramType,
	WatchNextProgram,
	WatchNext as WatchNextSpec,
} from "./specs/watch-next.nitro";

export type * from "./specs/watch-next.nitro";

export type WatchNext = Omit<WatchNextSpec, "insert"> & {
	insert(program: WatchNextProgram & { type: ProgramType }): Promise<number>;
};

// the watch next row only exists on android tv
export const WatchNext: WatchNext | null =
	Platform.OS === "android"
		? NitroModules.createHybridObject<WatchNextSpec>("WatchNext")
		: null;
