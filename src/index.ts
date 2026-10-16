import { NitroModules } from "react-native-nitro-modules";
import type { WatchNext as WatchNextSpec } from "./specs/watch-next.nitro";

export const WatchNext =
	NitroModules.createHybridObject<WatchNextSpec>("WatchNext");
