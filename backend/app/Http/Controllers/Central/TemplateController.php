<?php

namespace App\Http\Controllers\Central;

use Illuminate\Http\Request;
use App\Models\Template;

class TemplateController
{
    public function index()
    {
        $items = Template::query()->orderBy('id')->get(['id','key','name','version','enabled']);
        return response()->json(['success' => true, 'data' => $items]);
    }

    public function update(Request $request, $id)
    {
        $tpl = Template::find($id);
        if (!$tpl) return response()->json(['success' => false, 'message' => '模板不存在'], 404);
        $data = $request->validate([
            'name' => 'sometimes|string|max:128',
            'version' => 'sometimes|string|max:32',
            'enabled' => 'sometimes|boolean',
        ]);
        $tpl->fill($data);
        $tpl->save();
        return response()->json(['success' => true, 'data' => $tpl]);
    }
}
