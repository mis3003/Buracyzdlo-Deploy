
import { Song } from "./Song";

export interface Playlist {
    id?: number;
    name: string;
    description?: string;

    songs?: Song[]; // Add this if your backend includes songs
}
