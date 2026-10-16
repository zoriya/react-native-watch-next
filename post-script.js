// nitrogen expects the implementation in `com.margelo.nitro.watchnext`, ours is in `dev.zoriya.watchnext`.
const path = require("node:path");
const { readFile, writeFile } = require("node:fs/promises");

const androidWorkaround = async () => {
	const file = path.join(
		process.cwd(),
		"nitrogen/generated/android/WatchNextOnLoad.cpp",
	);
	const str = await readFile(file, { encoding: "utf8" });
	await writeFile(file, str.replace(/com\/margelo\/nitro\//g, "dev/zoriya/"));
};
androidWorkaround();
