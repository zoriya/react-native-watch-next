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

export const WatchNext: WatchNext | null =
	Platform.OS === "android" && Platform.isTV
		? NitroModules.createHybridObject<WatchNextSpec>("WatchNext")
		: null;
