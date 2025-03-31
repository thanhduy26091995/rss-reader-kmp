import SwiftUI
import Shared

struct ContentView: View {
    @StateObject private var viewModel = MainViewModel()
    
    var body: some View {
        MainScreen(
            onPostClicked: { post in
                if let link = post.link {
                    UIApplication.shared.open(URL(string: link)!)
                }
            },
            onEditClicked: {
                // TODO: Implement edit functionality
            }
        )
    }
}

class MainViewModel: ObservableObject {
    @Published var feeds: [Feed] = []
    @Published var selectedFeed: Feed?
    @Published var isLoading = false
    
    private let store: FeedStore
    
    init() {
        store = FeedStore(rssReader: RssReader.create())
        observeState()
        refresh()
    }
    
    private func observeState() {
        store.observerState().collect { state in
            DispatchQueue.main.async {
                self.feeds = state.feeds
                self.selectedFeed = state.selectedFeed
                self.isLoading = state.progress
            }
        }
    }
    
    func refresh() {
        store.dispatch(action: FeedAction.Refresh(forceLoad: true))
    }
    
    func selectFeed(_ feed: Feed?) {
        store.dispatch(action: FeedAction.SelectedFeed(feed: feed))
    }
}

struct MainScreen: View {
    let onPostClicked: (Post) -> Void
    let onEditClicked: () -> Void
    @StateObject private var viewModel = MainViewModel()
    
    var posts: [Post] {
        (viewModel.selectedFeed?.posts ?? viewModel.feeds.flatMap { $0.posts })
            .sorted { $0.date > $1.date }
    }
    
    var body: some View {
        VStack {
            ScrollView {
                LazyVStack(spacing: 16) {
                    ForEach(posts, id: \.link) { post in
                        PostItem(post: post, onPostClicked: onPostClicked)
                    }
                }
                .padding()
            }
            .refreshable {
                viewModel.refresh()
            }
            
            MainFeedBottomBar(
                feeds: viewModel.feeds,
                selectedFeed: viewModel.selectedFeed,
                onFeedClicked: viewModel.selectFeed,
                onEditClicked: onEditClicked
            )
        }
    }
}

struct PostItem: View {
    let post: Post
    let onPostClicked: (Post) -> Void
    
    var body: some View {
        Button(action: { onPostClicked(post) }) {
            VStack(alignment: .leading, spacing: 12) {
                Text(post.title)
                    .font(.headline)
                    .foregroundColor(.primary)
                
                if let imageUrl = post.imageUrl {
                    AsyncImage(url: URL(string: imageUrl)) { image in
                        image
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                    } placeholder: {
                        Color.gray.opacity(0.3)
                    }
                    .frame(height: 180)
                    .clipped()
                }
                
                if let desc = post.desc {
                    Text(desc)
                        .font(.body)
                        .foregroundColor(.secondary)
                        .lineLimit(5)
                }
                
                Text(Date(timeIntervalSince1970: TimeInterval(post.date / 1000)), style: .date)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            .padding()
            .background(Color(.systemBackground))
            .cornerRadius(16)
            .shadow(radius: 8)
        }
    }
}

struct MainFeedBottomBar: View {
    let feeds: [Feed]
    let selectedFeed: Feed?
    let onFeedClicked: (Feed?) -> Void
    let onEditClicked: () -> Void
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 16) {
                FeedIcon(feed: nil, isSelected: selectedFeed == nil) {
                    onFeedClicked(nil)
                }
                
                ForEach(feeds, id: \.sourceUrl) { feed in
                    FeedIcon(feed: feed, isSelected: selectedFeed == feed) {
                        onFeedClicked(feed)
                    }
                }
                
                FeedIcon(feed: nil, isSelected: false) {
                    onEditClicked()
                }
            }
            .padding(.horizontal)
        }
        .padding(.vertical, 8)
    }
}

struct FeedIcon: View {
    let feed: Feed?
    let isSelected: Bool
    let onClick: () -> Void
    
    var body: some View {
        Button(action: onClick) {
            ZStack {
                Circle()
                    .fill(isSelected ? Color.accentColor : Color.clear)
                    .frame(width: 48, height: 48)
                
                Circle()
                    .fill(Color.primary)
                    .frame(width: 40, height: 40)
                
                if let feed = feed {
                    if let imageUrl = feed.imageUrl {
                        AsyncImage(url: URL(string: imageUrl)) { image in
                            image
                                .resizable()
                                .aspectRatio(contentMode: .fill)
                        } placeholder: {
                            Text(feed.shortName())
                                .foregroundColor(.white)
                        }
                        .frame(width: 40, height: 40)
                        .clipShape(Circle())
                    } else {
                        Text(feed.shortName())
                            .foregroundColor(.white)
                    }
                } else {
                    Image(systemName: "pencil")
                        .foregroundColor(.white)
                }
            }
        }
    }
}

extension Feed {
    func shortName() -> String {
        String(title.prefix(2)).uppercased()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
