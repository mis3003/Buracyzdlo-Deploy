
export function fetchYoutubeTitle(url: string): Promise<string> {
    // getYoutubeTitleNoEmbed(url)

    return Promise.resolve(getYoutubeTitleNoEmbed(url));
  }

  async function getYoutubeTitleNoEmbed(url: string): Promise<string> {
    const res = await fetch(`https://noembed.com/embed?url=${encodeURIComponent(url)}`);
    const data = await res.json();
    console.log(data.title);
    return data.title ?? 'Brak tytułu';
  }