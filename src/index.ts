import { Platform } from "react-native";
import { NitroModules } from "react-native-nitro-modules";
import type { WatchNext as WatchNextSpec } from "./specs/watch-next.nitro";

export type * from "./specs/watch-next.nitro";

// the watch next row only exists on android tv
export const WatchNext =
	Platform.OS === "android"
		? NitroModules.createHybridObject<WatchNextSpec>("WatchNext")
		: null;
