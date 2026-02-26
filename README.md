# HomeAssistant TilerBoard

## Tile ↔ Kit: many-to-many

Раньше `Tile` хранил прямой `linkedKitId`, что делало связь жёсткой (фактически one-to-many) и усложняло повторное использование одного tile в разных kit.

Теперь связь переведена на классическую many-to-many через промежуточную таблицу:

- `tiles` — только данные tile без привязки к конкретному kit.
- `kits` — данные kit.
- `tile_kit_cross_ref` — связи `tileId ↔ kitId`.

### Что переделано по слоям (инфраструктура)

1. **Local models (Room)**
   - `TileLocalDatabaseModel` больше не содержит `linkedKitId`.
   - Добавлена `TileKitCrossRefLocalDatabaseModel` для хранения связей.
   - Добавлен relation-модель `KitWithTilesLocalDatabaseModel` для удобной загрузки `Kit + tiles` через `@Relation` + `@Junction`.

2. **DAO**
   - Удалены запросы, завязанные на `linkedKitId`.
   - Добавлены методы:
     - получение `KitWithTiles` по `kitId`;
     - создание tile;
     - создание связи tile-kit;
     - подсчёт количества связей для kit.

3. **Repository**
   - `createTile(...)` теперь делает два шага:
     1) сохраняет tile;
     2) создаёт запись в `tile_kit_cross_ref`.
   - `getTilesByKitId(...)` читает relation `KitWithTiles` и маппит `tiles` в доменную модель.

4. **Database**
   - В `AppDatabase` добавлена новая entity `TileKitCrossRefLocalDatabaseModel`.
   - Версия базы повышена до `2`.
   - Для локальной разработки включён `fallbackToDestructiveMigration()`.

### Мини-чеклист для дальнейшей адаптации

Если будете расширять инфраструктуру дальше (например, удалить tile из конкретного kit, не удаляя tile глобально), обычно добавляют:

- `TileDao.unlinkTileFromKit(tileId, kitId)`;
- `TileDao.deleteTileIfOrphan(tileId)` (если после unlink не осталось связей);
- use-case на уровень domain (`DetachTileFromKitUseCase`);
- отдельный UI flow «открепить» vs «удалить полностью».


### Реализовано дополнительно: detach/unlink инфраструктура

Добавлены операции для корректного открепления tile от kit:

- `TileDao.unlinkTileFromKit(tileId, kitId)` — удаляет конкретную связь из `tile_kit_cross_ref`;
- `TileDao.deleteTileIfOrphan(tileId)` — удаляет tile из `tiles` только если у него не осталось связей;
- `TileRepository.detachTileFromKit(tileId, kitId)` — orchestration-метод: unlink + orphan-cleanup;
- `DetachTileFromKitUseCase` — use-case уровня domain для вызова из presentation/UI.
