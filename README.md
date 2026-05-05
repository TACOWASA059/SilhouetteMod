# SilhouetteMod

コマンドで指定したエンティティを、クライアント側で単色のシルエットとして描画します。

## 対応バージョン

- Minecraft: 1.20.1
- Forge: 47.x

## 使い方

管理者権限、またはコマンド権限レベル2以上で `/silhouette` を実行します。

### シルエット対象を追加・更新

```mcfunction
/silhouette add <targets>
```

色を省略した場合は黒になります。

### RGB色を指定して追加・更新

```mcfunction
/silhouette add <targets> <red> <green> <blue>
```

`red`、`green`、`blue` はそれぞれ `0` から `255` の整数です。

例:

```mcfunction
/silhouette add @e[type=minecraft:cow,limit=1,sort=nearest] 0 0 0
/silhouette add @e[type=minecraft:zombie,distance=..10] 255 0 0
/silhouette add @s 0 80 255
```

同じエンティティにもう一度 `add` を実行すると、対象は維持されたまま色だけ更新されます。

### シルエット対象から外す

```mcfunction
/silhouette remove <targets>
```

### すべて解除

```mcfunction
/silhouette clear
```

登録されているすべてのシルエット対象を解除します。

## 描画仕様

- 対象エンティティの通常描画 RenderType をシルエット用 shader に差し替えます。
- 対象がプレイヤーの場合、一人称視点の手もシルエット色で描画します。

## Figura連携

- Figura が導入されている場合、対象プレイヤーの Figura アバター描画にもシルエット色を適用します。
- Figura 側の RenderType をシルエット用 shader に差し替えて描画します。

## Optional Dependencies

- [Figura](https://github.com/FiguraMC/Figura) - 任意依存です。導入されている場合、対象プレイヤーの Figura アバターにもシルエット描画を適用します。
  - License: PolyForm Noncommercial License 1.0.0 
